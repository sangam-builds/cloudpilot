import React, { useState, useEffect } from 'react';
import { ticketApi } from '../../api/ticketApi';
import { useAuth } from '../../context/AuthContext';
import { MessageSquare, Sparkles, Send, Copy, Check } from 'lucide-react';

export const TicketCommentThread = ({ ticketId, onCommentAdded }) => {
  const { user } = useAuth();
  const [commentText, setCommentText] = useState('');
  const [suggestedReply, setSuggestedReply] = useState('');
  const [loadingAi, setLoadingAi] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [copied, setCopied] = useState(false);
  const [comments, setComments] = useState([
    {
      id: 1,
      author: 'System Auto-Assigner',
      text: 'Ticket created, classified, and assigned based on skill matching.',
      time: 'Just now'
    }
  ]);

  useEffect(() => {
    // Fetch AI Suggested Reply via RAG
    if (ticketId) {
      setLoadingAi(true);
      ticketApi.getSuggestedReply(ticketId)
        .then((res) => {
          if (res.suggestedReply) {
            setSuggestedReply(res.suggestedReply);
          }
        })
        .catch((err) => console.log('RAG reply suggestion error:', err))
        .finally(() => setLoadingAi(false));
    }
  }, [ticketId]);

  const handleSendComment = async (e) => {
    e.preventDefault();
    if (!commentText.trim()) return;

    setSubmitting(true);
    try {
      await ticketApi.addComment(ticketId, commentText);
      setComments((prev) => [
        ...prev,
        {
          id: Date.now(),
          author: user?.name || 'Support Agent',
          text: commentText,
          time: 'Just now'
        }
      ]);
      setCommentText('');
      if (onCommentAdded) onCommentAdded();
    } catch (err) {
      console.error('Failed to post comment', err);
    } finally {
      setSubmitting(false);
    }
  };

  const useAiReply = () => {
    setCommentText(suggestedReply);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <div style={{ marginTop: '24px' }}>
      <h4 style={{ fontSize: '1rem', fontWeight: 700, marginBottom: '14px', display: 'flex', alignItems: 'center', gap: '8px' }}>
        <MessageSquare size={16} />
        Activity &amp; Response Thread
      </h4>

      {/* AI Suggested Reply Card (RAG showcase) */}
      {suggestedReply && (
        <div style={{
          background: 'linear-gradient(135deg, rgba(99, 102, 241, 0.1) 0%, rgba(14, 165, 233, 0.08) 100%)',
          border: '1px solid rgba(99, 102, 241, 0.3)',
          borderRadius: '12px',
          padding: '16px 18px',
          marginBottom: '20px'
        }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '10px' }}>
            <span style={{ fontSize: '0.8rem', fontWeight: 700, color: '#818cf8', display: 'flex', alignItems: 'center', gap: '6px' }}>
              <Sparkles size={14} />
              AI Copilot Grounded Draft Reply (RAG + Knowledge Base)
            </span>
            <button
              type="button"
              onClick={useAiReply}
              className="btn btn-secondary"
              style={{ padding: '4px 10px', fontSize: '0.75rem', background: 'rgba(99, 102, 241, 0.2)', borderColor: '#6366f1' }}
            >
              {copied ? <Check size={12} color="#10b981" /> : <Copy size={12} />}
              {copied ? 'Applied to Input' : 'Use This Draft'}
            </button>
          </div>

          <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', whiteSpace: 'pre-line', lineHeight: 1.45 }}>
            {suggestedReply}
          </p>
        </div>
      )}

      {/* Comments List */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', marginBottom: '18px' }}>
        {comments.map((c) => (
          <div
            key={c.id}
            style={{
              padding: '12px 16px',
              background: 'rgba(15, 23, 42, 0.5)',
              border: '1px solid var(--border-subtle)',
              borderRadius: '10px'
            }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.75rem', color: 'var(--text-muted)', marginBottom: '4px' }}>
              <span style={{ fontWeight: 700, color: '#93c5fd' }}>{c.author}</span>
              <span>{c.time}</span>
            </div>
            <p style={{ fontSize: '0.85rem', color: 'var(--text-primary)' }}>{c.text}</p>
          </div>
        ))}
      </div>

      {/* Reply Box */}
      <form onSubmit={handleSendComment} style={{ display: 'flex', gap: '10px' }}>
        <input
          type="text"
          className="form-input"
          placeholder="Type a response or use the AI draft above..."
          value={commentText}
          onChange={(e) => setCommentText(e.target.value)}
        />
        <button
          type="submit"
          disabled={submitting || !commentText.trim()}
          className="btn btn-primary"
          style={{ whiteSpace: 'nowrap' }}
        >
          <Send size={15} />
          Send
        </button>
      </form>
    </div>
  );
};
